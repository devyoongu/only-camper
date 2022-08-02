if (document.getElementById('partnerItemCount')) {
    let context = document
        .getElementById('partnerItemCount')
        .getContext('2d');

    // var paramMap = JSON.stringify({});

    $.ajax({
        url: "/aggregate/partner-itemcount",
        type: "GET",
        contentType: "application/json",
    })
    .done(function (fragment) {
        console.log(fragment);

        let data = fragment.data;
        let xArray = [];
        let xData = [];

        for (let i = 0; i < data.length; i++) {
            xArray.push(data[i].partnerName);
            xData.push(data[i].itemCount);
        }

        let chart = new Chart(context, {
                type: 'bar', // 차트의 형태
                data: { // 차트에 들어갈 데이터
                    //x 축
                    labels: xArray,
                    datasets: [
                        { //데이터
                            label: '상품 등록건수', //차트 제목
                            fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
                            data: xData,
                            backgroundColor: [
                                //색상
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(153, 102, 255, 0.2)',
                                'rgba(255, 159, 64, 0.2)'
                            ],
                            borderColor: [
                                //경계선 색상
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1 //경계선 굵기
                        }
                    ]
                },
                options: {
                    title: {
                        display: true,
                        text: "파트너별 상품 등록현황 - Top7",
                        fontSize: 20,
                        // fontColor: "rgba(0, 0, 255, 0.25)",
                    },
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    stepSize: 1, //y축 간격
                                    beginAtZero: true
                                }
                            }
                        ]
                    }
                }
            });
    });
}

if (document.getElementById('partnerOrderCount')) {
    let context = document
        .getElementById('partnerOrderCount')
        .getContext('2d');

    $.ajax({
        url: "/aggregate/partner-ordercoount",
        type: "GET",
        contentType: "application/json",
    })
    .done(function (fragment) {
        console.log(fragment);

        let data = fragment.data;
        let xArray = [];
        let xData = [];

        for (let i = 0; i < data.length; i++) {
            xArray.push(data[i].partnerName);
            xData.push(data[i].orderCount);
        }

        let chart = new Chart(context, {
                type: 'bar', // 차트의 형태
                data: { // 차트에 들어갈 데이터
                    //x 축
                    labels: xArray,
                    datasets: [
                        { //데이터
                            label: '상품 판매건수', //차트 제목
                            fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
                            data: xData,
                            backgroundColor: [
                                //색상
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(153, 102, 255, 0.2)',
                                'rgba(255, 159, 64, 0.2)'
                            ],
                            borderColor: [
                                //경계선 색상
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1 //경계선 굵기
                        }
                    ]
                },
                options: {
                    title: {
                        display: true,
                        text: "파트너별 판매현황 - Top7",
                        fontSize: 20,
                        // fontColor: "rgba(0, 0, 255, 0.25)",
                    },
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    stepSize: 1, //y축 간격
                                    // suggestedMin: 0,//y축 최소 값
                                    // suggestedMax: 100,//y축 최대값
                                    beginAtZero: true
                                }
                            }
                        ]
                    }
                }
            });
    });
}

if (document.getElementById('itemOrderCount')) {
    let context = document
        .getElementById('itemOrderCount')
        .getContext('2d');

    $.ajax({
        url: "/aggregate/item-ordercoount",
        type: "GET",
        contentType: "application/json",
    })
    .done(function (fragment) {
        console.log(fragment);

        let data = fragment.data;
        let xArray = [];
        let xData = [];

        for (let i = 0; i < data.length; i++) {
            xArray.push(data[i].itemName);
            xData.push(data[i].orderCount);
        }

        let chart = new Chart(context, {
                type: 'bar', // 차트의 형태
                data: { // 차트에 들어갈 데이터
                    //x 축
                    labels: xArray,
                    datasets: [
                        { //데이터
                            label: '상품 판매건수', //차트 제목
                            fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
                            data: xData,
                            backgroundColor: [
                                //색상
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(153, 102, 255, 0.2)',
                                'rgba(255, 159, 64, 0.2)'
                            ],
                            borderColor: [
                                //경계선 색상
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1 //경계선 굵기
                        }
                    ]
                },
                options: {
                    title: {
                        display: true,
                        text: "상품별 판매현황 - Top7",
                        fontSize: 20,
                        // fontColor: "rgba(0, 0, 255, 0.25)",
                    },
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    stepSize: 1, //y축 간격
                                    beginAtZero: true
                                }
                            }
                        ]
                    }
                }
            });
    });
}

if (document.getElementById('orderDateCount')) {
    let context = document
        .getElementById('orderDateCount')
        .getContext('2d');

    $.ajax({
        url: "/aggregate/order-datecount",
        type: "GET",
        contentType: "application/json",
    })
    .done(function (fragment) {
        console.log(fragment);

        let data = fragment.data;
        let xArray = [];
        let xData = [];

        for (let i = 0; i < data.length; i++) {
            xArray.push(data[i].orderDate);
            xData.push(data[i].orderCount);
        }

        let chart = new Chart(context, {
                type: 'line', // 차트의 형태
                data: { // 차트에 들어갈 데이터
                    //x 축
                    labels: xArray,
                    datasets: [
                        { //데이터
                            label: '상품 판매건수', //차트 제목
                            fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
                            data: xData,
                            backgroundColor: [
                                //색상
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(153, 102, 255, 0.2)',
                                'rgba(255, 159, 64, 0.2)'
                            ],
                            borderColor: [
                                //경계선 색상
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1 //경계선 굵기
                        }
                    ]
                },
                options: {
                    title: {
                        display: true,
                        text: "일자별 판매현황 - 최근 일주일",
                        fontSize: 20,
                        // fontColor: "rgba(0, 0, 255, 0.25)",
                    },
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    stepSize: 1, //y축 간격
                                    beginAtZero: true
                                }
                            }
                        ]
                    }
                }
            });
    });
}