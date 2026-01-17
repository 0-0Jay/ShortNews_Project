import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowUp } from "@fortawesome/free-solid-svg-icons";
const scrollTop = () => {
  window.scrollTo(0, 0);
};

function ScrollUp() {
  return (
    <div className="scroll-up">
      <button type="button" onClick={scrollTop}>
        <FontAwesomeIcon icon={faArrowUp} />
      </button>
    </div>
  );
}

export default React.memo(ScrollUp);
