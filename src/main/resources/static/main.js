//Variable to hold game data
let data;

//update the heading text
function setHeadingText(headingText) {
    let heading = document.getElementById('heading');
    heading.innerText = headingText;
}

//to show sections
function showSections(sections) {
    setVisibility('choices', sections.indexOf('choices') > -1);
    setVisibility('board', sections.indexOf('board') > -1);
    setVisibility('loading', sections.indexOf('loading') > -1);
}

function startFreshGame() {
    showSections(["choices"]);
}


function main() {
    startFreshGame();
}


// playing game
async function modeSelected(mode){
    showSections(["loading"]);
    let response = await save(`/tictactoe/api/v1/startGame?mode=${mode}`,'GET')
    data = response;

    //populateBox
    populateBox()
    setPlayer2(mode);
    showSections(["board"]);
    togglePlayer();
    setHeadingText(`Game Begin! ${data.player} to play`);
    
}

function setPlayer2(mode){
    let plyer2Tab = document.getElementById('player2');
    plyer2Tab.innerText = mode === 'PLAYERvsAI' ? 'Computer' : 'Player 2';
}


async function boxClicked(e) {
    let x = e.srcElement.dataset.x;
    let y = e.srcElement.dataset.y;

    //return if clicked on non empty cell
    //or if the game has ended
    if(data.board[x][y] === 'X' || data.board[x][y] === 'O' || data.gameStatus !== "ONGOING") return;

    //fill up the board
    data.board[x][y] = data.symbol;
    data.index = {x,y}

    //register move with server
    let saveResponse = await save('/tictactoe/api/v1/registerMove', 'POST', data);
    data = saveResponse;
    setHeadingText(`Greate Move! ${data.player} to play Next`);

    //preparing game for next state
    populateBox();
    togglePlayer();
    if(data.gameStatus !== "ONGOING") {
        if(data.winningIndexes) markWinningBoxes(data.winningIndexes);
        resetIfGameEnded();
    }
}


// lock state if game has ended, either play won or game tied.
function resetIfGameEnded(){
    setHeadingText(`${data.winner} won this Game. Click on Play Again button to play again`);
    setVisibility('playAgain', true);
}

function markWinningBoxes(winningIndexes){
    let playBoard = document.getElementById('playBoard');

    for(let box of winningIndexes){
        let index = box.row * 3 + box.column;
        playBoard.childNodes[index].classList.add('win-box')
    }
}

// set visibility of element by id
function setVisibility(id, show) {
    let element = document.getElementById(id);
    if(show)
        element.classList.remove('hidden');
    else
        element.classList.add('hidden');
}

//play again clicked
function playAgain(){
    setVisibility('playAgain', false);
    showSections(["choices"]);
    setHeadingText('Thanks for playing again. Select the mode you want to play')
}

//toggle player by chance
function togglePlayer(){
    let player1 = document.getElementById('player1');
    let player2 = document.getElementById('player2');

    if(data.player === 'PLAYER1'){
        player1.classList.remove('inactive')
        player2.classList.add('inactive')  
    } else {
        player2.classList.remove('inactive')  
        player1.classList.add('inactive')
    }
}

function populateBox() {
    let playBoard = document.getElementById('playBoard');

    //cleaning board
    playBoard.innerHTML = "";

    for (let x = 0; x < 3; x++) {
        for (let y = 0; y < 3; y++) {
            playBoard.appendChild(getSpanElt(x, y, data.board[x][y]));
        }
    }
}

function getSpanElt(x, y, mark) {
    let span = document.createElement('span');
    span.innerText = mark;
    span.classList.add(mark === 'X' ? 'cross' : 'zero');
    span.setAttribute('data-x', x);
    span.setAttribute('data-y', y);
    return span;
}


// type - 'GET' or 'POST'
async function save(url, type, postData = {}) {
    let response = type === 'GET' ?
        await fetch(url) :
        await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(postData)
        });

    let result;

    if (response.ok) {
        result = await response.json();
    } else {
        result = "HTTP-Error: " + response.status;
    }

    return result;
}
