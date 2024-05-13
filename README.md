# Acquire game

## Description
This project implements test harness, unit tests and Integration tests for executing Acquire game command operations using Game Tree.

game-results.txt should be available after one successful run of the tests/game start operation in directory ```${project_root_directory}/static/output/```

Test inputs are present in `project09/src/test/resources`.

## Executing Tests

### Using IDE command line
1. Open the code base in and IDE
2. Open a terminal or command prompt.
3. Navigate to the project directory
4. Run the following command to execute the tests:
    ```bash
   gradle test
    ```

### Using IDE
1. Import the project into your preferred Integrated Development Environment (IDE) with Spring Utilities installed.
2. Locate the `Project09ApplicationTests.java` file under `project09/src/test/java/com/oosd/project09/`.
3. Run the `Project09ApplicationTests.java` file to execute all tests. 

### For Testing GameTree Implementation & GameTreeStrategyMove Tests
1. Run the following command (in the current project directory)
```bash
gradle test --tests 'GameTreeTest'
```
2. The command will execute current player's move via GameTree creating all possible state nodes and then 
returning the leaf node of the branch selected based on the strategy.
3. The json string in the file `UniTests/GameTreeTest` can be changed to use a different strategy for generating game tree
and executing moves using the same.


### For executing all tests using terminal
1. Pick the latest commit.
2. Navigate to directory project09
`Note: Since with game tree the moves might take longer to execute for each player, its better to run tests individually 
using previous gradle command mentioned above, because all tests will include running 100 tests which will take significantly long time.`
3. run command 
```bash 
 gradle clean test 
 ```
or
```bash 
 gradle clean build
 ```
4. build command will generate jar as well as run the tests.

### For sending requests to the service after executing jar
1. Running jar file using command
```bash
java -jar project09-0.0.1-SNAPSHOT.jar
```
2. The service would have been started.
3. Then using the following command for turn request you can send and get expected json outputs for different inputs, 
following is an example curl command for file `in2.json`

```bash
curl --location 'http://localhost:8080/turn' \
--header 'Content-Type: application/json' \
--data '{
  "player": {
    "player": "One",
    "cash": 10000,
    "shares": [
      {"share": "Continental", "count": 5},
      {"share": "Festival", "count": 5},
      {"share": "Imperial", "count": 6}
    ],
    "tiles": [
      {"row": "C", "column": "3"},
      {"row": "H", "column": "11"},
      {"row": "H", "column": "12"},
      {"row": "I", "column": "10"},
      {"row": "I", "column": "11"},
      {"row": "I", "column": "12"}
    ]
  },
  "board": {
    "tiles": [
      {"row": "B", "column": "1"},
      {"row": "B", "column": "2"},
      {"row": "A", "column": "3"},
      {"row": "B", "column": "4"},
      {"row": "B", "column": "5"},
      {"row": "B", "column": "6"},
      {"row": "B", "column": "7"},
      {"row": "D", "column": "3"},
      {"row": "E", "column": "3"}
    ],
    "hotels": [
      {
        "hotel": "American",
        "tiles": [
          {"row": "B", "column": "4"},
          {"row": "B", "column": "5"},
          {"row": "B", "column": "6"},
          {"row": "B", "column": "7"}
        ]
      },
      {
        "hotel": "Continental",
        "tiles": [
          {"row": "D", "column": "3"},
          {"row": "E", "column": "3"}
        ]
      },
      {
        "hotel": "Festival",
        "tiles": [
          {"row": "B", "column": "1"},
          {"row": "B", "column": "2"}
        ]
      }
    ]
  },
  "tile": [
    {"row": "A", "column": "1"},
    {"row": "A", "column": "2"},
    {"row": "A", "column": "4"},
    {"row": "A", "column": "5"},
    {"row": "A", "column": "6"},
    {"row": "A", "column": "7"},
    {"row": "A", "column": "8"},
    {"row": "A", "column": "9"},
    {"row": "A", "column": "10"},
    {"row": "A", "column": "11"}
  ],
  "share": [
    {"share": "American", "count": 10},
    {"share": "Continental", "count": 4},
    {"share": "Festival", "count": 5},
    {"share": "Imperial", "count": 6},
    {"share": "Sackson", "count": 12},
    {"share": "Tower", "count": 18},
    {"share": "Worldwide", "count": 24}
  ],
  "xhotel": [
    {"hotel": "Sackson"},
    {"hotel": "Imperial"},
    {"hotel": "Tower"},
    {"hotel": "Worldwide"}
  ]
}
'
```
