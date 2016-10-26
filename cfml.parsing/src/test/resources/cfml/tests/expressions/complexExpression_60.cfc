component{

   function name(){
    var params = {
      thingId = { "value" = thing.getID(), "cfsqltype" = "CF_SQL_INTEGER" },
      name = { "value" = thing.getName(), "cfsqltype" = "CF_SQL_VARCHAR", null = !len( thing.getName() ) }
    };
   }
}