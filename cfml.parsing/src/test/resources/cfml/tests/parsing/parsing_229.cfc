/**
* Some explanation.
*/
component {

  /**
  * Some function explanation.
  *
  * @localData Something.
  * @keyToCheck Something else.
  */
  public string function extractInfo(required struct localData, required string keyToCheck) {
    if (!isNull(localData)) {
      if (structKeyExists(localData, keyToCheck)) {
          return localData[keyToCheck];
      }
    }
  }
}