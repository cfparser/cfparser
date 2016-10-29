
component{
public void function test() {
  param string parse_error="but works";
  param type="string" name="parses_fine" default="works as expected";
  writeDump(parse_error);
  writeDump(parses_fine);
}
}