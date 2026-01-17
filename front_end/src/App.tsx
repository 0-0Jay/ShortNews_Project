import { Route, Routes, Navigate } from "react-router-dom";
import "./App.css";
import "./Moblie.css";
import NewsMobile from "./pages/NewsMobile";
import NewPasswd from "./pages/NewPasswd";
import MissPasswd from "./pages/MissPasswd";
import LoginPasswd from "./pages/LoginPasswd";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Main from "./pages/Main";
import MyPage from "./pages/MyPage";
import WithDraw from "./pages/WithDraw";
import BookMark from "./pages/BookMark";
import MyLog from "./pages/MyLog";
import News from "./pages/News";
import MissId from "./pages/MissId";
import FindId from "./pages/FindId";
import ReactModal from "react-modal";
import Social from "./pages/SocialProcess";
import { SearchKeywordProvider } from "./context/SearchKeywordContext";
import { DateProvider } from "./context/DateContext";
import NotFound from "./pages/NotFound";
import InfiniteScroll from "./components/InfiniteScroll";
import Search from "./pages/Search";
declare global {
  interface Window {
    Kakao: any;
  }
}
function App() {
  ReactModal.setAppElement("#root");
  // 검색창 열림 유무 (0:닫힘, 1:열었다가 닫음, 2:열림)
  // const [isSearchOpen, setSearchOpen] = useState(0);

  return (
    <DateProvider>
      <SearchKeywordProvider>
            <Routes>

              <Route path="/main/:category" element = {<Main></Main>}></Route>
              <Route path = "/main/search" element = {<Search></Search>}></Route>

              <Route path="/signup" element={<SignUp />}></Route>

              <Route path="/LoginPasswd" element={<LoginPasswd />}></Route>

              <Route path="/" element={<Login />}></Route>

              <Route path="/MissId" element={<MissId />}></Route>

              <Route path="/MissPasswd" element={<MissPasswd />}></Route>

              <Route path="/FindId" element={<FindId />}></Route>

              <Route path="/NewPasswd" element={<NewPasswd />}></Route>
              <Route path="/member" element={<MyPage />}></Route>
              <Route path="/WithDraw" element={<WithDraw />}></Route>
              <Route path="/BookMark/:cateid?" element={<BookMark />}></Route>
              <Route path="/myActivity/:status" element={<MyLog />}></Route>
              <Route path="/selectNews/:newsid" element={<News />}></Route>
              <Route path="/login/oauth2/*" element={<Social />}></Route>
              <Route path="/pageNotFound" element={<NotFound />}></Route>
              <Route path="/Scrolltest" element={<InfiniteScroll />}></Route>
              <Route path="/shareNews/:newsid" element={<NewsMobile />}></Route>
              <Route path="*" element={<Navigate to="/pageNotFound" />}></Route>
            </Routes>
      </SearchKeywordProvider>
    </DateProvider>
  );
}

export default App;
