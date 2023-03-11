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

let $videoStream = document.getElementById('sec-video');
let $videoBtn = document.getElementById('sec-video-btn');
let $videoIconBtn = document.getElementById('sec-video-buttons');
let $emtNameList = document.querySelectorAll('.emotion-name');
let $emtPercList = document.querySelectorAll('.emotion-percentage');

document.querySelector("#header-logo-img").addEventListener('click', () => { location.reload()});

/** 메뉴 slideOut-In */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelector("#header-menu-svg")
        .addEventListener("click", (e) => {
            if (document.querySelector("aside").classList.contains("on")) {
                //메뉴 slideOut
                document.querySelector("aside").classList.remove("on");
                document.querySelector("section").classList.remove("on");
            } else {
                //메뉴 slideIn
                document.querySelector("aside").classList.add("on");
                document.querySelector("section").classList.add("on");
            }
        });
});

/** 분석 버튼 클릭 이동 */
document.querySelector("#aside-analize-btn").addEventListener("click", (e) => {
    //if( isCamWorking == false ) { alert('카메라 접근을 허용해주세요.'); return;}

    axios.post(baseURL+"/image/end", JSON.stringify({userId:1}), JSONConfig)
    .then(response => {
        console.log(response.data);
        location.href="./analize/analize.html";
    });
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

/* Web Cam 비디오에서 사진 캡처 함수 */
function takeSnapshot() {
    var $myCanvasElement = document.getElementById('sec-canvas');
    var myCTX = $myCanvasElement.getContext('2d');

    myCTX.drawImage($videoStream, 0, 0, 640, 480);
}

/* 오토캡처 메소드 */
function takeAuto() {
    takeSnapshot();
    uploadCanvasToServer(); // get snapshot right away then wait and repeat

    setInterval( () => {
        takeSnapshot();
        uploadCanvasToServer();
    }, 1000);  //캡처 빈도 : 1초
}

/** 서버로 캔버스 이미지 업로드 */
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
    // formData.append('imageFile', file, fileName);
    formData.append('imageFile', file, fileName);
    formData.append('imageName', fileName);
    formData.append('userId', 1);

    //7. axios의 post 메소드를 사용하여 서버에 전송
    axios.post(baseURL+"/image/new", formData, FormConfig)
        .then( response => {
            var data = response.data;
            console.log(`Response.data : ${JSON.stringify(data)}`);

            renderTable(data);
        })
        .catch( err => {
            console.log(err);
        });
};

function renderTable(data) { //객체 데이터 받아서 테이블에 렌더링
  let tbodyData = ``;

  for (const key in data) {
    if(data[key] == null) continue;
    if(key == "score" || key == "localTime")  continue; 

    tbodyData = tbodyData + `
    <tr>
        <td class="emotion-name">${key}</td>
        <td class="emotion-percentage">${data[key]}%</td>
    </tr>
      `
  }
  document.querySelector('#aside-emotions > tbody').innerHTML = tbodyData;
}