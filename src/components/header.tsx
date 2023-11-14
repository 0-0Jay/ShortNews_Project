import logo from "../assets/logo.svg";
import "../css/App.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass } from "@fortawesome/free-solid-svg-icons";
import { faBars } from "@fortawesome/free-solid-svg-icons";
import { faBell } from "@fortawesome/free-solid-svg-icons";

function Header() {
  let member_name = "IST";
  return (
    <div className="nav-bar">
      <div className="nav-bar-column"></div>
      <div className="nav-bar-column">
        <img src={logo}></img>
      </div>
      <div className="nav-bar-column">
        <FontAwesomeIcon icon={faMagnifyingGlass} size="2x" />
        
        <FontAwesomeIcon icon={faBell} size="2x" />
        <span className="nav-notification"></span>
        <FontAwesomeIcon icon={faBars} size="2x" />
      </div>
    </div>
  );
}

export default Header;
