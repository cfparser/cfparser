component {
    static {
        static['test'] = 'value';
    }

    static public function printTest() {
        static['key'] = 'newValue';
        static.key2 = 'string';
        compName::key = 'string3';
        writeOutput(static.test);
        writeOutput(compName::test);
        compName::runStatic();
        long.component.name::member.function();
    }
}