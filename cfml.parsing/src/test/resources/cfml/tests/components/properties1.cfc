/**
* @author 
*/
component accessors="true"{
  
  // The first name of the User
  property type="string" name="firstName";
  // Last name of the User
  property name="lastName";

  /**
  * Constructor
  */
  function init(){
    return this;
  }

  /**
  * Submit an order
  * @product.hint The product object
  * @coupon.hint The Coupon code needed
  * @results.hint The results object
  */
  function submitOrder( required product, coupon="", boolean results=true ){

  }

}