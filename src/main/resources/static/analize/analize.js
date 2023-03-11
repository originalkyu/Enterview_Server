/***** Config *****/

//let baseURL = "http://syg.ddns.net:9636"
let baseURL = "http://localhost:9636"

var isCamWorking = false;

const JSONConfig = {
    headers : {
        'Content-Type' : `application/json`,
        'Access-Control-Allow-Origin': "*"
}};
const FormConfig = {
    headers : {
        'Content-Type': 'multipart/form-data',
        'Access-Control-Allow-Origin': "*"
}};

/* ----- Header ----- */

var $cover = document.querySelector("#cover-div");
var $videoStream = document.getElementById('sec-video');
var $videoBtn = document.getElementById('sec-video-btn');
var $score = document.getElementById('aside-evaluation-score');
var $videoIconBtn = document.getElementById('sec-video-buttons');
let $timer = document.getElementById('sec-timer');

document.querySelector("#header-logo-img").addEventListener('click', () => { location.reload()});

document.addEventListener("DOMContentLoaded", () => {
    document.querySelector("#header-menu-svg")
      .addEventListener("click", (e) => {
        if (document.querySelector("aside").classList.contains("on")) {
          //메뉴 slideOut
          document.querySelector("aside").classList.remove("on");
          document.querySelector("section").classList.remove("on");
          //slideOut시 menuBtn의 img src를 menu icon으로 변경
          //document.getElementById("menuBtn").src = "./menuBtn.png";
        } else {
          //메뉴 slideIn
          document.querySelector("aside").classList.add("on");
          document.querySelector("section").classList.add("on");
          //slideIn시 menuBtn의 img src를 cross icon으로 변경
          //document.getElementById("menuBtn").src = "./cross.png";
        }
      });
});

/* 결과창으로 이동 */
document.querySelector("#aside-result-btn").addEventListener("click", (e) => {
  //서버에서 받아온 결과 데이터를 result 페이지로 전달
  if( isCamWorking == false ) { alert('카메라 버튼을 눌러 테스트를 시작해주세요.'); return; }

  axios.post(baseURL+"/image/end", JSON.stringify({userId:1}), JSONConfig)
  .then(response => {
    if (window.confirm('검사가 종료되었습니다. 결과 생성중입니다.')) {
      localStorage.setItem('resultData', JSON.stringify(response.data) );
      location.href = `../result/result.html`;
    } else {
      alert("왜 싸가지없게 취소 누르시죠? 결과창으로 이동합니다.");
    }
  })
  .catch(err => { 
    alert(`결과창 생성 실패\nERR : ${err}`);
    console.log(`결과창 생성 실패\nERR : ${err}`); });
});

/* Web Cam 비디오 입력 함수 */
function getVideo(){
  navigator.getMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
  navigator.getMedia( {video: true, audio: false},
      (stream) => {
          $videoStream.srcObject = stream;
          $videoStream.play();
          isCamWorking = true;
          $videoIconBtn.style.display = 'none';
          startTimer(0, $timer);

          axios.post(baseURL+"/image", JSON.stringify({userId:1}), JSONConfig)
              .then(response => {
                  console.log(response.data);
                  takeAuto();
              });
      },
      (error) => {
          isCamWorking = false;
          alert('카메라 접근 실패');
      });
}

/* 사진 캡처 메소드 */
function takeSnapshot() {
  var $myCanvasElement = document.getElementById('sec-canvas');
  var myCTX = $myCanvasElement.getContext('2d');
  
  //myCTX.drawImage($videoStream, 0, 0, myCanvasElement.width, myCanvasElement.height);
  myCTX.drawImage($videoStream, 0, 0, 640, 480);
}

function uploadCanvasToServer() {
  const canvas = document.getElementById('sec-canvas');

  //1. Canvas 이미지를 데이터로 저장
  const imgBase64 = canvas.toDataURL('image/jpeg', 'image/octet-stream'); //캔버스 요소를 base64값으로 변환하였고 저장시 포맷은 image/jpeg로 설정
  
  //2. 저장된 Canvas 이미지를 base64에서 디코딩
  const decodImg = atob(imgBase64.split(',')[1]);

  //3. 디코딩된 값을 바이트 배열로 변환 후 저장
  let array = [];
  for (let i = 0; i < decodImg .length; i++) {
    array.push(decodImg .charCodeAt(i));
  }

  //4. typed array인 8bit unsigned array로 변환

  //5. new blob() 생성자를 사용해 blob 값으로 변환
  const file = new Blob([new Uint8Array(array)], {type: 'image/jpeg'});
  const fileName = 'canvas_img_' + new Date().getMilliseconds() + '.jpg';

  //6. FormData() 생성자를 사용해 이미지 값을 서버의 데이터로 저장
  
  let formData = new FormData();
  formData.append('imageFile', file, fileName);
  formData.append('imageName', fileName);
  formData.append('userId', 1);

  //7. axios의 post 메소드를 사용하여 서버에 전송
  axios.post(baseURL+"/image/new", formData, FormConfig)
      .then( response => {
        var data = response.data;
        console.log(`Response.data : ${JSON.stringify(data)}`);

        $score.innerText = `${data.score * 100}`;
        renderTable(data);
      })
      .catch( err => {
          console.log(err);
      });
}

/* 오토캡처 메소드 */
function takeAuto() {
  takeSnapshot();
  uploadCanvasToServer(); // get snapshot right away then wait and repeat
    setInterval( () => {
      uploadCanvasToServer();
      takeSnapshot();
    }, 1000);  //캡처 빈도 : 1초 
}

function startTimer(time, obj){
  var hour, min, sec;

  var timer = setInterval( () => {

    time++; // 1초마다 증가, 타이머의 경우 time--;

    min = Math.floor(time/60);
    hour = Math.floor(min/60);
    sec = time%60;
    min = min%60;

    var th = hour;
    var tm = min;
    var ts = sec;
    
    // 한자리일 경우 처리
    if(th < 10){
        th = "0" + hour;
    }
    if(tm < 10){
        tm = "0" + min;
    }
    if(ts < 10){
        ts = "0" + sec;
    }

    // 함수 호출 당시 받은 object의 html 교체
    obj.innerHTML = th + ":" + tm + ":" + ts;
    // returnHTML = th.toString + ":" + tm.toString + ":" + ts.toString;
    // return returnHTML;
  }
, 1000);
}

function renderTable(data) { //객체 데이터 받아서 테이블에 렌더링
  let tbodyData = ``;

  for (const key in data) {
    if( data[key] == null ) continue;
    if( key == "localTime" )  continue; 
    if( key == "score" ) continue;

    tbodyData = tbodyData + `
    <tr>
        <td class="emotion-name">${key}</td>
        <td class="emotion-percentage">${data[key]}%</td>
    </tr>
      `
  }
  document.querySelector('#aside-emotions > tbody').innerHTML = tbodyData;
}