component {

	function dotrim(data, callback){
		callback(trim(data));
	}

	function bar(){
		dotrim(function("hello world"){
			//...
		});
	}

}
