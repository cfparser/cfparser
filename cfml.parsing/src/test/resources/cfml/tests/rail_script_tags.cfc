log text="Event Gateway #variables.id# started" file=this.logfile;
log  
    text = "text"
    type = "information"
    application = "yes"
    file = "filename"
    log = "log type"; 
log text="Event Gateway #variables.id# error: #ex.message#" file=this.logfile type="error";
param name="url.age" type="numeric" default="10" max="100" min="18";
//How about cfhttp ?
http method="GET" url="http://www.google.com" result="webPage";
mail from="Mark@getrailo.com" to="gert@getrailo.com" subject="Awesome! Tags in Script!"{
WriteOutput("Hey Gert!
Check out the code samples here! You can write tags in CFScript
Mark
");
}
//Query a database
query name="getUsers" dataSource="myDatasource"{
echo("SELECT * FROM tusers WHERE UserID =");
queryparam cfsqltype="varchar" value="6300EE15-1320-5CC2-F9F48B9DBBA54D9F";
}
dump(getUsers);