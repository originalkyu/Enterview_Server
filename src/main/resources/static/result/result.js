const $avrEmotionsItems = document.querySelector("#avr-emotions-items");

if( localStorage.getItem('resultData') ){
    var rawResult = localStorage.getItem('resultData');
    var resultData = JSON.parse(rawResult);

    console.log(resultData);
    applyData(resultData);
} else {
    alert("에러가 발생했습니다. 재시작해주세요.")
}

function insertAllTableItems(target, emotions) {
    var tempHTML = ``;
    for (var i in emotions) {
        tempHTML = `
            <tr>
                <td>${i}</td>
                <td>${emotions[i]}%</td>
            </tr>
        `
        target.insertAdjacentHTML('beforeend', tempHTML);
    }
}

function applyData(res) { 
    document.querySelector('#total-time').innerText = `${res.totalTIme}`;
    
    insertAllTableItems($avrEmotionsItems, res.avrEmotion);

    document.querySelector('#best-score').innerText = `${res.highScore * 100}점`;
    document.querySelector('#worst-score').innerText = `${res.lowestScore * 100}점`;
    document.querySelector('#positive-duration').innerText = `${res.goodTime}`;
    document.querySelector('#negative-duration').innerText = `${res.badTime}`;

    document.querySelector('#total-score').innerText = `${res.avrScore * 100}/100`
    document.querySelector('#comment').innerText = `${res.comment}`
}