let stripe_key = "sk_test_4eC39HqLyjWDarjtT1zdp7dc"; //Secret Key here
let Stripe = require("stripe")(stripe_key);

Parse.Cloud.define("purchaseItem", async (request) => {
    let item, order;
    let itemQuery = new Parse.Query('Item');    
    itemQuery.equalTo('ItemName', request.params.ItemName);

    const result = await itemQuery.first(null,{useMasterKey: true});

    if(!result){
        throw new Error('Sorry, this item is no longer available.');
    } else if(result.get('quantityAvailable') <= 0){
        throw new Error('Sorry, this item is out of stock.');
    } else {
        item = result;
        const charge = await Stripe.charges.create({
            amount: item.get('Price')*100, // It needs to convert to cents
            currency: "usd",
            source: request.params.cardToken,
            description: "Charge for " + request.params.email
        })
                
        item.increment('quantityAvailable', -1);
        const object = await item.save(null,{useMasterKey: true})
        const order = new Parse.Object("Order");
        order.set('name', request.params.name);
        order.set('email', request.params.email);
        order.set('address', request.params.address);
        order.set('zip', request.params.zip);
        order.set('city_state', request.params.city_state);
        order.set('item', item.get('ItemName'));
        order.set('fulfilled', true);
        order.set('charged', false);
        order.set('stripePaymentId', charge.id);            
        order.set('charged', true);
        await order.save(null,{useMasterKey:true});
    }
    return 'OK';
});