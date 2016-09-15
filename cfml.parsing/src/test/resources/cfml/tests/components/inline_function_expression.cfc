component {

	function dotrim(data, callback){
		callback(trim(data));
	}

	function bar(){
		dotrim("hello world", function(result){

		});
	}

}
