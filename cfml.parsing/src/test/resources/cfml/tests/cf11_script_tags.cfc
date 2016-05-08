    cfhttp(url="www.google.com", method="GET");
       Cfhttp(URL="http://#CGI.SERVER_NAME#.../target.cfm", method="GET")
       {
             cfhttpparam(type="url", name='emp_name', value="Awdhesh");
             cfhttpparam(type="header", name='myheader', value="My custom header");
       }
       Writeoutput(cfhttp.filecontent); 