package game2048;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score;
    int maxTile;
    boolean isSaveNeeded = true;
    Stack<Integer> previousScores = new Stack<>();
    Stack<Tile[][]> previousStates = new Stack<>();

    public Model() {
        resetGameTiles();
        score = 0;
        maxTile = 2;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public void resetGameTiles() {


        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();

    }

    private void addTile() {
        List<Tile> list = getEmptyTiles();
        if (list.size() == 0)
            return;
        Tile tile = list.get((int) (list.size() * Math.random()));
        tile.value = Math.random() < 0.9 ? 2 : 4;

        for (Tile[] i : gameTiles) {
            for (Tile j : i) {
                if (j.equals(tile)) {
                    j = tile;
                }
            }
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    list.add(gameTiles[i][j]);
                }
            }
        }
        return list;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean hasChanged = false;

        for (int i = 0; i < tiles.length - 1; i++) {
            if (tiles[i].value == 0 && tiles[i+1].value != 0) {
                Tile _temp = tiles[i];
                tiles[i] = tiles[i + 1];
                tiles[i + 1] = _temp;
                i = -1;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean hasChanges = false;

        for (int i = 0; i < FIELD_WIDTH - 1;) {
            if (tiles[i].value == tiles[i+1].value){
                if (tiles[i].value == tiles[i+1].value && tiles[i].value == 0) {
                    i++;
                    continue;
                }

                tiles[i].value = tiles[i].value + tiles[i+1].value;
                tiles[i+1].value = 0;
                maxTile = tiles[i].value > maxTile ? tiles[i].value : maxTile;
                score += tiles[i].value;
                i += 2;

                hasChanges = true;
            }else {
                i++;
            }
        }
        compressTiles(tiles);

        return hasChanges;
    }

    public void left() {
        if (isSaveNeeded)
            saveState(gameTiles);
        boolean changed = false;
        for (Tile[] i:
             gameTiles) {
            if (compressTiles(i) |
            mergeTiles(i))
                changed = true;

        }
        if (changed) addTile();

        isSaveNeeded = true;
    }

    public void rotateMatrix() {

        Tile tmp;
        for (int i = 0; i < FIELD_WIDTH / 2; i++) {
            for (int j = i; j < FIELD_WIDTH - 1 - i; j++) {
                tmp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[FIELD_WIDTH - j - 1][i];
                gameTiles[FIELD_WIDTH - j - 1][i] = gameTiles[FIELD_WIDTH -i - 1][FIELD_WIDTH - j - 1];
                gameTiles[FIELD_WIDTH - i - 1][FIELD_WIDTH - j - 1] = gameTiles[j][FIELD_WIDTH - i - 1];
                gameTiles[j][FIELD_WIDTH - i - 1] = tmp;
            }
        }

    }

    public void right() {
        saveState(gameTiles);
        rotateMatrix();
        rotateMatrix();
        left();
        rotateMatrix();
        rotateMatrix();
    }

    public void up() {
        saveState(gameTiles);
        rotateMatrix();
        rotateMatrix();
        rotateMatrix();
        left();
        rotateMatrix();
    }

    public void down() {
        saveState(gameTiles);
        rotateMatrix();
        left();
        rotateMatrix();
        rotateMatrix();
        rotateMatrix();
    }

    boolean canMove()
    {
        if (!getEmptyTiles().isEmpty())
            return true;
        for (int i = 0; i < gameTiles.length; i++)
            for (int j = 1; j < gameTiles.length; j++)
                if (gameTiles[i][j].value == gameTiles[i][j - 1].value)
                    return true;


        for (int i = 0; i < gameTiles.length; i++)
            for (int j = 1; j < gameTiles.length; j++)
                if (gameTiles[j][i].value == gameTiles[j - 1][i].value)
                    return true;


        return false;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tiles1 = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                tiles1[i][j] = new Tile(tiles[i][j].value);
            }
        }

        previousStates.push(tiles1);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.empty() & !previousScores.empty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0: left();
            break;
            case 1: right();
            break;
            case 2: down();
            break;
            case 3: up();
            break;
        }
    }

    public boolean hasBoardChanged() {

        int totalScoreCurrent = 0;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                totalScoreCurrent += gameTiles[i][j].value;
            }
        }

        int totalScorePeek = 0;
        Tile[][] tiles = previousStates.peek();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                totalScorePeek += tiles[i][j].value;
            }
        }

        return (totalScoreCurrent - totalScorePeek) != 0;

    }

    public MoveEfficiency getMoveEfficiency(Move move) {

        move.move();

        int emptyTiles = -1;
        int score1 = 0;

        if (hasBoardChanged()) {
            emptyTiles = getEmptyTiles().size();
            score1 = score;
        }

        rollback();

        return new MoveEfficiency(emptyTiles, score1, move);
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.<MoveEfficiency>reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        priorityQueue.poll().getMove().move();
    }
}
