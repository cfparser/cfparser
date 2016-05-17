component {

private void function compileSource() hint="compile dynamic source" {
		var dir = 0;
		var path = 0;

		var paths = 0;
		var file = 0;
		var counter = 1;
		var len = 0;
		var directories = 0;

		//do check to see if the compiled jar is already there
		var jarName = calculateJarName(getSourceDirectories());
		var jar = getCompileDirectory() & "/" & jarName;

    if (fileExists(jar)) {
        if (isTrustedSource()) {
            /* add that jar to the classloader */
            file = createObject("java", "java.io.File").init(jar);
            getURLClassLoader().addURL(file.toURL());
            return ;
        } else {
            file action="delete" file="#jar#" {};
        }
    }

	try {
	    path = getCompileDirectory() & "/" & createUUID();

		directory action="create" directory="#path#"{};
			//first we copy the source to our tmp dir
			directories = getSourceDirectories();
			len = arraylen(directories);
			for(; counter lte len; counter = counter + 1)
			{
				dir = directories[counter];
				$directoryCopy(dir, path);
			}

			//then we compile it, and grab that jar

			paths = ArrayNew(1); //have to write it this way so CF7 compiles
			ArrayAppend(paths, path);

			jar = getJavaCompiler().compile(paths, getURLClassLoader(), jarName);

		/* add that jar to the classloader */
		file = createObject("java", "java.io.File").init(jar);
		getURLClassLoader().addURL(file.toURL());

		/* delete the files */
		if (directoryExists(path)) {
			directory action="delete" recurse="true" directory="#path#"{};
		}

        /* save the file for when trusted source is on -*/
		if (fileExists(jar) AND NOT isTrustedSource()) {
			file action="delete" file="#jar#"{}
		}

		} catch (any e) {
			/* make sure the files are deleted */
			if (directoryExists(path)) {
				directory action="delete" recurse="true" directory="#path#"{};
			}

			rethrow;
		}
	}
}
}