export const CopyUrl = async (shareLink: any) => {
  await window.navigator.clipboard
    .writeText("www.shortnews.kr" + shareLink)
    .then((res) => {
      console.log(res);
      alert("클립보드에 복사되었습니다.");
    })
    .catch((error) => {
      alert("복사 실패");
    });
};

export const shareKakao = (Article: any, newsid?:string) => {
  if (window.Kakao) {
    const kakao = window.Kakao;
    if (!kakao.isInitialized()) {
      kakao.init(process.env.REACT_APP_KAKAO_JS);
    }

    kakao.Link.sendDefault({
      objectType: "feed",
      content: {
        title: Article.title,
        description: Article.content,
        imageUrl:
          Article.imgs !== null
            ? Article.imgs[0]
            : process.env.REACT_APP_S3_SHORT + "assets/news-default.svg",
        link: {
          // [내 애플리케이션] > [플랫폼] 에서 등록한 사이트 도메인과 일치해야 함
          mobileWebUrl: "http://www.shortnews.kr/shareNews/" + newsid,
          webUrl: "http://www.shortnews.kr/selectNews/" + newsid,
          // mobileWebUrl: "http://localhost:3000",
          // webUrl: "http://localhost:3000",
        },
      },
      social: {
        likeCount: Article.like,
        viewCount: Article.views
      },
      buttons: [
        {
          title: "자세히 보러 가기",
          link: {
            mobileWebUrl: "http://www.shortnews.kr/shareNews/" + newsid,
            webUrl: "http://www.shortnews.kr/selectNews/" + newsid,
        },
        },
      ],
    });
  }
};

// export const shareKakao = (Article: any, newsid?: string) => {
//   if (window.Kakao) {
//     const kakao = window.Kakao;
//     if (!kakao.isInitialized()) {
//       kakao.init(process.env.REACT_APP_KAKAO_JS);
//     }
//     kakao.Share.sendDefault({
//       objectType: "text",
//       text: `[${Article.title}]\n\n${Article.content}`,
//       link: {
//         mobileWebUrl: "http://www.shortnews.kr/shareNews/" + newsid,
//         webUrl: "http://www.shortnews.kr/selectNews/" + newsid,
//       },
//       buttons: [
//         {
//           title: "웹으로 보기",
//           link: {
//             mobileWebUrl: "http://www.shortnews.kr/shareNews/" + newsid,
//             webUrl: "http://www.shortnews.kr/selectNews/" + newsid,
//             // mobileWebUrl: "http://localhost:3000",
//             // webUrl: "http://localhost:3000",
//           },
//         },
//       ],
//     });
//   }
// };

export function shareNaverBlog(Article: any, newsid?: string) {
  var url = encodeURI("http://www.shortnews.kr");
  var title = encodeURI(Article.title);
  var shareURL =
    "https://share.naver.com/web/shareView.nhn?url=" + url + "&title=" + title;
  window.open(shareURL, "share", "width=500, height=500");
}

export function shareFacebook(Article: any, newsid?: string) {
  const sendText = encodeURI(Article.title); // 전달할 text
  const sendUrl = encodeURI("http://www.shortnews.kr"); // 전달할 URL
  // const sendUrl = "http://localhost:3000/selectNews/" + newsid; // 전달할 URL
  const URL = `http://www.facebook.com/sharer/sharer.php?u=${sendUrl}&p=${sendText}`;
  window.open(URL, "share", "width=500, height=500");
}
