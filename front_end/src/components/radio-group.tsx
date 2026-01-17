import React, { useState } from "react";

interface RadioGroupProps {
  options: string[];
  className: string;
}

const RadioGroup: React.FC<RadioGroupProps> = ({ options, className }) => {
  const [selectedOption, setSelectedOption] = useState<string | null>(
    options[0]
  );

  const radioSelect = (option: string) => {
    setSelectedOption((prevSelectedOption) =>
      prevSelectedOption === option ? null : option
    );
  };

  return (
    <div className="radio-group-container">
      {options.map((option) => (
        <label key={option} className="cate-check-label">
          <input
            type="radio"
            value={option}
            checked={selectedOption === option}
            onChange={() => radioSelect(option)}
            className={className}
          />
          <span className={selectedOption === option ? "cate-checked" : ""}>
            {option}
          </span>
        </label>
      ))}
    </div>
  );
};

export default RadioGroup;
