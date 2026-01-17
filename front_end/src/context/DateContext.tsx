import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  Dispatch,
  SetStateAction,
} from "react";

interface DateContextProps {
  date: Date;
  selectedDate: Date | null;
  setSelectedDate: Dispatch<SetStateAction<Date | null>>;
  handleDateChange: (date: Date | null) => void;
}

const DateContext = createContext<DateContextProps | undefined>(undefined);

interface DateProviderProps {
  children: ReactNode;
}

export const DateProvider: React.FC<DateProviderProps> = ({
  children,
}: DateProviderProps) => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const date = selectedDate === null ? new Date() : selectedDate;
  const handleDateChange = (date: Date | null) => {
    setSelectedDate(date);
  };

  return (
    <DateContext.Provider
      value={{ date, selectedDate, setSelectedDate, handleDateChange }}
    >
      {children}
    </DateContext.Provider>
  );
};

export const useDate = (): DateContextProps => {
  const context = useContext(DateContext);
  if (!context) {
    throw new Error("useDate은 DateProvider 내에서 사용되어야 합니다");
  }
  return context;
};
