public static ConsoleCommand of(String commandAsString, String[] parameters) {
    try{
        ConsoleCommand command = ConsoleCommand.valueOf(commandAsString);
        command.parameters = parameters;
        return command;
    } catch(IllegalArgumentException e){
        return ConsoleCommand.UNKNOWN;
    }
}