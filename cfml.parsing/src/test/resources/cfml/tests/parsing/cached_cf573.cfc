component{
public boolean function getAPIVersion(
    required numeric requestedAPIVersion
) cachedwithin=createTimeSpan(1,0,0,0) {
    return (variables.availableAPIVersions.find(arguments.requestedAPIVersion) > 0) ?
        arguments.requestedAPIVersion :
        variables.availableAPIVersions[1];
}
}