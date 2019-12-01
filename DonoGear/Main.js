let stripe_key = "sk_test_tsAWwq2H1mSx37s3jP70y9Uq00eF1RyNUT";
let Stripe = require("stripe")(stripe_key);

Parse.Cloud.define("purchaseItem", async (request) => {
   let item, order;
   let itemQuery = new Parse.Query('Collectible');
   itemQuery.equalTo('objectId', request.params.collectibleId);
   const result = await itemQuery.first(null,{useMasterKey: true});

   if(!result){
        throw new Error('Sorry, this item is no longer available.');
   } else {
        item = result;
        const charge = await Stripe.charges.create({
          amount: request.params.price * 100, // It needs to convert to cents
          currency: "usd",
          source: request.params.cardToken,
          description: "Charge for " + request.params.email
        })

       const object = await item.save(null,{useMasterKey: true})
       const order = new Parse.Object("Order");
       order.set('name', request.params.name);
       order.set('email', request.params.email);
       order.set('address', request.params.address);
       order.set('zip', request.params.zip);
       order.set('city_state', request.params.city_state);
       order.set('collectibleId', request.params.collectibleId);
       order.set('userId', request.params.userId);
       order.set('raffleTicketCount', request.params.raffleTicketCount);
       order.set('price', request.params.price);
       order.set('fulfilled', true);
       order.set('charged', false);
       order.set('stripePaymentId', charge.id);
       order.set('charged', true);
       await order.save(null,{useMasterKey:true});
   }

   return 'OK';
});

Parse.Cloud.define("saveCard", async (request) => {
   let customer;
   let customerId;

   if (request.params.customerId !== null) {
       customer = await Stripe.customers.update(request.params.customerId, {
            source: request.params.cardToken,
            name: request.params.name
       })

       customerId = customer.id;
   } else {

       customer = await Stripe.customers.create({
            source: request.params.cardToken,
            name: request.params.name
       })

       customerId = customer.id;
   }

   if (! customerId) {
        throw new Error('Unable to save card');
   }

   return customerId;
});
