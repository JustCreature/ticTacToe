package Game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameActionListener implements ActionListener {
  private int row;
  private int cell;
  private GameButton button;

  public GameActionListener(int row, int cell, GameButton gButton) {
    this.row = row;
    this.cell = cell;
    this.button = gButton;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    GameBoard board = button.getBoard();

    if (board.isTurnable(row, cell)) {
      updateByPlayersData(board);

      if (board.isFull()) {
        board.getGame().sendMessage("Draw");
        // TODO: Исправлено - перестановка символа необходима,
        //  иначе после ничьей человек ходит символами компьютера
        board.getGame().passTurn();
        board.emptyField();
      } else {
        // TODO: Исправлено - ненужный ход компьютера и перестановка
        //  символа прри победе человека
        if (!board.getGame().getCurrentPlayer().isRealPlayer()) {
//          updateByAiData(board);
          aiTurn(board);
        }
      }
    } else {
      board.getGame().sendMessage("Incorrect turn");
    }

  }

  private void updateByAiData(GameBoard board) {
    int x, y;
    Random rnd = new Random();
    do {
      x = rnd.nextInt(GameBoard.dimension);
      y = rnd.nextInt(GameBoard.dimension);
    } while (!board.isTurnable(x, y));

    board.updateGameField(x, y);

    int cellIndex = GameBoard.dimension * x + y;
    board.getButton(cellIndex).setText(Character.toString(board.getGame().getCurrentPlayer().getPlayerSign()));

    if (board.checkWin()) {
      button.getBoard().getGame().sendMessage("Computer won!");
      board.emptyField();
      // TODO: Исправлено перепутан символ после победы компьютера
      board.getGame().passTurn();
    } else {
      board.getGame().passTurn();
    }
  }

  private void updateByPlayersData(GameBoard board) {
    board.updateGameField(row, cell);

    button.setText(Character.toString(board.getGame().getCurrentPlayer().getPlayerSign()));

    if (board.checkWin()) {
      button.getBoard().getGame().sendMessage("You won!");
      board.emptyField();
    } else {
      board.getGame().passTurn();
    }
  }

// __________________________________________________________________________________________


  public void aiTurn(GameBoard board) {
    // TODO: Добавлено - Реализация умного компьютера, все что находится
    //  ниже данной строчки относится к реализации умного компьютера
    int x;
    int y;
    Random random = new Random();

    int[] searchWin = aiSearchWin(board.getGame().getCurrentPlayer().getPlayerSign(), board);
    int[] searchBlock = aiSearchWin('X', board);
    int[] searchPosition = aiSearchPosition(board);
    if(searchWin[0]==1) {
      y = searchWin[1];
      x = searchWin[2];
    } else
    if(searchBlock[0]==1) {
      y = searchBlock[1];
      x = searchBlock[2];
    } else
    if(searchPosition[0]>=1) {
      y = searchPosition[1];
      x = searchPosition[2];
    } else
      do {
        y = random.nextInt(GameBoard.dimension);
        x = random.nextInt(GameBoard.dimension);
      } while (!board.isTurnable(y, x));
    board.updateGameField(x, y);

    int cellIndex = GameBoard.dimension * x + y;
    board.getButton(cellIndex).setText(Character.toString(board.getGame().getCurrentPlayer().getPlayerSign()));

    if (board.checkWin()) {
      button.getBoard().getGame().sendMessage("Computer won!");
      board.emptyField();
      board.getGame().passTurn();
    } else {
      board.getGame().passTurn();
    }

  }

  public  static int[] aiSearchPosition(GameBoard board) {

    int SIZE = GameBoard.dimension;
    int DOTS_TO_WIN = SIZE;
    char DOT_X = 'X';
    char DOT_EMPTY = GameBoard.getNullSymbol();
    char[][] map = board.getGameField();

    int[][] mapPosition = new int[SIZE][SIZE];
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        mapPosition[i][j] = 0;
      }
    }

    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {

        if((i+1-DOTS_TO_WIN) >= 0 && (j-1+DOTS_TO_WIN) < SIZE) {
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i-k][j+k] == DOT_X) break;
            if ((k+1) == DOTS_TO_WIN) {
              for (int l = 0; l < DOTS_TO_WIN; l++) {
                if(map[i-l][j+l] == DOT_EMPTY) {
                  mapPosition[i-l][j+l] ++;
                }
              }
            }
          }
        }

        if((j-1+DOTS_TO_WIN) < SIZE) {
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i][j+k] == DOT_X) break;
            if ((k+1) == DOTS_TO_WIN) {
              for (int l = 0; l < DOTS_TO_WIN; l++) {
                if(map[i][j+l] == DOT_EMPTY){
                  mapPosition[i][j+l] ++;
                }
              }
            }
          }
        }

        if((i-1+DOTS_TO_WIN) < SIZE && (j-1+DOTS_TO_WIN) < SIZE) {
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i+k][j+k] == DOT_X) break;
            if ((k+1) == DOTS_TO_WIN) {
              for (int l = 0; l < DOTS_TO_WIN; l++) {
                if(map[i+l][j+l] == DOT_EMPTY) {
                  mapPosition[i+l][j+l] ++;
                }
              }
            }
          }
        }

        if((i-1+DOTS_TO_WIN) < SIZE) {
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i+k][j] == DOT_X) break;
            if ((k+1) == DOTS_TO_WIN) {
              for (int l = 0; l < DOTS_TO_WIN; l++) {
                if(map[i+l][j] == DOT_EMPTY) {
                  mapPosition[i+l][j] ++;
                }
              }
            }
          }
        }
      }
    }

    int[] searchPosition = new int[]{0,0,0};

    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        if (mapPosition[i][j] > searchPosition[0]) {
          searchPosition = new int[]{mapPosition[i][j],i,j};
        }
      }
    }

    return searchPosition;
  }

  public static int[] aiSearchWin(char c, GameBoard board) {

    int SIZE = GameBoard.dimension;
    int DOTS_TO_WIN = SIZE;
    char DOT_X = 'X';
    char DOT_EMPTY = GameBoard.getNullSymbol();
    char[][] map = board.getGameField();

    int[] coordBlock = new int[]{0,0,0};
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {

        if((i+1-DOTS_TO_WIN) >= 0 && (j-1+DOTS_TO_WIN) < SIZE) {
          int skip = 0;
          int cordSkipX = 0;
          int cordSkipY = 0;
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i-k][j+k] != c && (skip == 1 || map[i-k][j+k] != DOT_EMPTY)) {
              break;
            } else {
              if(map[i-k][j+k] == DOT_EMPTY) {
                skip++;
                cordSkipX = i - k;
                cordSkipY = j + k;
              }
            }
            if ((k+1) == DOTS_TO_WIN && skip == 1) {
              return coordBlock = new int[]{1,cordSkipX,cordSkipY};
            }
          }
        }

        if((j-1+DOTS_TO_WIN) < SIZE) {
          int skip = 0;
          int cordSkipX = 0;
          int cordSkipY = 0;
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i][j+k] != c && (skip == 1 || map[i][j+k] != DOT_EMPTY)) {
              break;
            } else {
              if(map[i][j+k] == DOT_EMPTY) {
                skip++;
                cordSkipX = i;
                cordSkipY = j+k;
              }
            }
            if ((k+1) == DOTS_TO_WIN && skip==1) {

              return coordBlock = new int[]{1,cordSkipX,cordSkipY};
            }
          }
        }

        if((i-1+DOTS_TO_WIN) < SIZE && (j-1+DOTS_TO_WIN) < SIZE) {
          int skip = 0;
          int cordSkipX = 0;
          int cordSkipY = 0;
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i+k][j+k] != c && (skip == 1 || map[i+k][j+k] != DOT_EMPTY)) {
              break;
            } else {
              if(map[i+k][j+k] == DOT_EMPTY) {
                skip++;
                cordSkipX = i + k;
                cordSkipY = j + k;
              }
            }
            if ((k+1) == DOTS_TO_WIN && skip==1) {
              return coordBlock = new int[]{1,cordSkipX,cordSkipY};
            }
          }
        }

        if((i-1+DOTS_TO_WIN) < SIZE) {
          int skip = 0;
          int cordSkipX = 0;
          int cordSkipY = 0;
          for (int k = 0; k < DOTS_TO_WIN; k++) {
            if(map[i+k][j] != c && (skip == 1 || map[i+k][j] != DOT_EMPTY)) {
              break;
            } else {
              if(map[i+k][j] == DOT_EMPTY) {
                skip++;
                cordSkipX = i + k;
                cordSkipY = j;
              }
            }
            if ((k+1) == DOTS_TO_WIN && skip==1) {
              return coordBlock = new int[]{1,cordSkipX,cordSkipY};
            }
          }
        }
      }
    }
    return coordBlock;
  }

}





