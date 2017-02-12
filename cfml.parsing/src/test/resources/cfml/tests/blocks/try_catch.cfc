try {
    throw(message="Oops", detail="xyz"); //CF9+
} catch (any e) {
    WriteOutput("Error: " & e.message);
    rethrow; //CF9+
} catch (any e) {
    WriteOutput("Error: " & e.message);
    rethrow; //CF9+
} finally { //CF9+
    WriteOutput("I run even if no error");
}