import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String castle = "\uD83C\uDFF0";

        int sizeBoard = 5;
        int step = 0;

        int defeatedMonsters = 0;

        Person person = new Person(sizeBoard);

        Random random = new Random();
        int castleX = 1 + random.nextInt(sizeBoard);
        int castleY = 1;

        String[][] board = new String[sizeBoard][sizeBoard];
        for (int y = 1; y <= sizeBoard; y++) {
            for (int x = 1; x <= sizeBoard; x++) {
                board[y - 1][x - 1] = "  ";
            }
        }

        int countMonster = sizeBoard * sizeBoard - sizeBoard - 1 - 5;
        Monster[] monsters = new Monster[countMonster];
        int count = 0;

        while (count < countMonster) {
            Monster monster;

            if (random.nextBoolean()) {
                monster = new Monster(sizeBoard);
            } else {
                monster = new BigMonster(sizeBoard);
            }

            int row = monster.getY() - 1;
            int column = monster.getX() - 1;

            if (board[row][column].equals("  ")) {
                board[row][column] = monster.getImage();
                monsters[count] = monster;
                count++;
            }
        }

        board[castleY - 1][castleX - 1] = castle;

        // Зелье исцеления
        String potion = "\uD83E\uDDEA";
        while (true) {
            int potionX = random.nextInt(sizeBoard);
            int potionY = random.nextInt(sizeBoard);
            if (board[potionY][potionX].equals("  ")  && !(potionX == person.getX() - 1
                    && potionY == person.getY() - 1)) {
                board[potionY][potionX] = potion;
                break;
            }
        }

        // Казино
        String casino = "🎰";
        while (true) {
            int casinoX = random.nextInt(sizeBoard);
            int casinoY = random.nextInt(sizeBoard);
            if (board[casinoY][casinoX].equals("  ")
                    && !(casinoX == person.getX() - 1 && casinoY == person.getY() - 1)) {
                board[casinoY][casinoX] = casino;
                break;
            }
        }

        System.out.println("Привет! Ты готов начать играть в игру? (Напиши: ДА или НЕТ)");

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        System.out.println("Ваш ответ:\t" + answer);

        switch (answer.trim().toUpperCase()) {
            case "ДА":

                System.out.println("Выбери сложность игры (от 1 до 5):");
                int difficultGame = readInt(scanner);
                System.out.println("Выбранная сложность:\t" + difficultGame);

                System.out.println("\n=== Обозначения на карте ===");
                System.out.println(person.getImage() + " — Вы (Герой)");
                System.out.println(castle + " — Замок (Цель игры)");
                System.out.println("🧟 — Обычный монстр");
                System.out.println("👹 — Большой монстр");
                System.out.println(potion + " — Зелье исцеления (+1 жизнь)");
                System.out.println(casino + " — Казино ");
                System.out.println("===========================\n");
                while (true) {
                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                    outputBoard(board, person.getLive());

                    System.out.println("Введите, куда будет ходить персонаж "
                            + "(ход возможен только по вертикали или горизонтали на одну клетку)"
                            + "\nКоординаты персонажа — (x: " + person.getX()
                            + ", y: " + person.getY() + ")");

                    int x = readInt(scanner);
                    int y = readInt(scanner);
                    System.out.println(x + ", " + y);

                    if (person.isMoveCorrect(x, y)) {
                        if (board[y - 1][x - 1].equals("  ")) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            step++;

                            System.out.println("Ход корректный; новые координаты: "
                                    + person.getX() + ", " + person.getY()
                                    + "\nХод номер: " + step);
                        } else if (board[y - 1][x - 1].equals(castle)) {
                            System.out.println("Вы прошли игру! \nШагов сделано: " + step + "\nМонстров убито: " + defeatedMonsters);
                            break;
                        } else if (board[y - 1][x - 1].equals(potion)) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            person.upLive();
                            step++;
                            System.out.println("Вы нашли зелье исцеления! +1 жизнь.");
                        } else if (board[y - 1][x - 1].equals(casino)) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            step++;

                            System.out.println("Вы попали в казино!");
                            // jackpot
                            if (random.nextInt(100) < 10) {
                                System.out.println("ДЖЕКПОТ! Вы мгновенно победили в игре!");
                                break;
                            }
                            // 50/50
                            int coin = 1 + random.nextInt(2);
                            System.out.println("Выберите: 1 или 2");
                            int guess = readInt(scanner);

                            if (guess == coin) {
                                person.upLive();
                                System.out.println("Повезло! Вы получаете +1 жизнь!");
                            } else {
                                person.downLive();
                                System.out.println("Не повезло! Вы теряете 1 жизнь!");
                            }
                        } else {
                            for (Monster monster : monsters) {
                                if (monster.conflictPerson(x, y)) {
                                    if (monster.taskMonster(difficultGame)) {
                                        board[person.getY() - 1][person.getX() - 1] = "  ";
                                        person.move(x, y);
                                        defeatedMonsters++;
                                        step++;
                                    } else {
                                        person.downLive();
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println("Некорректный ход");
                    }

                    if (person.getLive() <= 0) {
                        break;
                    }
                }

                if (person.getLive() <= 0) {
                    System.out.println("\uD83D\uDC80 Закончились жизни. Вы продержались " + step + "шагов и победили " + defeatedMonsters + "монстров!");
                }
                break;

            case "НЕТ":
                System.out.println("Жаль, приходи ещё!");
                break;

            default:
                System.out.println("Данные введены некорректно");
                break;


        }
    }

    static void outputBoard(String[][] board, int live) {
        String leftBlock = "| ";
        String rightBlock = "|";
        String wall = "  + —— + —— + —— + —— + —— +";

        System.out.println("     1    2    3    4    5");

        for (int y = 0; y < board.length; y++) {
            System.out.println(wall);
            System.out.print((y + 1) + " ");

            for (String col : board[y]) {
                System.out.print(leftBlock + col + " ");
            }
            System.out.println(rightBlock);
        }
        System.out.println(wall);

        System.out.println("Жизни:\t" + "❤️ ".repeat(Math.max(0, live)) + "\n");
    }
    static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Ошибка! Нужно ввести целое число. Попробуйте ещё раз:");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
