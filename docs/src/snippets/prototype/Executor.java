class CommandExecutor {

    private final Map<ConsoleCommand, Command> commands;

    public void execute(ConsoleCommand consoleCommand) {
        commands.get(consoleCommand).proceed(consoleCommand.getParameters());
    }
}

