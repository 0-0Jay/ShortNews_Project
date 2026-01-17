import { useState } from "react";
import DatePicker from "react-datepicker";
// import "react-datepicker/dist/react-datepicker.css";
import "../react-datepicker.css";
import { ko } from "date-fns/esm/locale";

interface CalendarProps {
  onDateChange: (date: Date) => void;
  getNews : any;
  selected: Date;
}

function Calender({ onDateChange, selected, getNews }: CalendarProps) {
  const [startDate, setStartDate] = useState<Date | null>(new Date());

  const handleDateChange = (date: Date) => {
    setStartDate(date);
    onDateChange(date);
    getNews(date);
  };

  return (
    <DatePicker
      locale={ko}
      className="datepicker"
      dateFormat="yyyy년 MM월 dd일"
      selected={selected}
      onChange={(date: any) => handleDateChange(date)}
      minDate={new Date("2024-01-10")}
      maxDate={new Date()}
      inline
    />
  );
}

export default Calender;
