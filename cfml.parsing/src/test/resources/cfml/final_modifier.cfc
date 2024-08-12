final component {
    static {
        static['test'] = 'value';
    }

    final public function printTest() {
        static['key'] = 'newValue';
        static.key2 = 'string';
        compName::key = 'string3';
        writeOutput(static.test);
        writeOutput(compName::test);
        compName::runStatic();
    }

    final static public function printTest2() {
        static['key'] = 'newValue';
        static.key2 = 'string';
        compName::key = 'string3';
        writeOutput(static.test);
        writeOutput(compName::test);
        compName::runStatic();
    }
}