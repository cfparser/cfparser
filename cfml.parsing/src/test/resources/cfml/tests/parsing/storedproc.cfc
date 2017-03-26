function foo(){
    cfstoredproc(
         procedure="dbo.myTestProcedure",
         datasource=Variables.datasource
    ) {
         cfprocparam(value=myId, cfsqltype="cf_sql_integer");
         cfprocresult(resultset=1, name="myQuery");
    }
    Variables.myTest = [];
}