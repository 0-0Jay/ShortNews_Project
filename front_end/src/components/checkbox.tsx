import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { IconDefinition } from "@fortawesome/free-solid-svg-icons";

interface CheckboxProps {
  icon?: IconDefinition;
  label: string;
  className: string;
  isCheckedParent : boolean;
  onCheckChange?: (isChecked: boolean) => void;
}

const Checkbox: React.FC<CheckboxProps> = ({
  icon,
  label,
  className,
  onCheckChange,
  isCheckedParent,
}) => {
  const [isChecked, setChecked] = useState(isCheckedParent);
  const [isHovered, setHovered] = useState(false);
  useEffect(()=>{
    setChecked(isCheckedParent);
  }, [isCheckedParent])
  const CheckboxCheck = () => {
    setChecked((prevChecked) => !prevChecked);
    if (onCheckChange != null) onCheckChange(!isChecked);
  };
  return (
    <div>
      <label
        className={
          className == "circle-check"
            ? "circle-check-label"
            : "cate-check-label"
        }
      >
        <input
          type="checkbox"
          checked={isChecked}
          onChange={CheckboxCheck}
          className={className}
        />
        <span
          className={
            isHovered
              ? label == "IT/과학"
                ? "IT과학"
                : label == "생활/문화"
                ? "생활문화"
                : label
              : isChecked && className != "circle-check"
              ? label == "IT/과학"
                ? "IT과학"
                : label == "생활/문화"
                ? "생활문화"
                : label
              : ""
          }
        >
          {icon && <FontAwesomeIcon icon={icon} />}
          &nbsp;
          {label}
        </span>
      </label>
    </div>
  );
};

export default Checkbox;
