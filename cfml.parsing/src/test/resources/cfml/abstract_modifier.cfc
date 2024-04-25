abstract component {
    static {
        static['test'] = 'value';
    }

    final static public function printTest() {
        static['key'] = 'newValue';
        static.key2 = 'string';
        compName::key = 'string3';
        writeOutput(static.test);
        writeOutput(compName::test);
        compName::runStatic();
    }

    abstract static public function printAbsStTest();

    abstract public function printAbsTest();
}