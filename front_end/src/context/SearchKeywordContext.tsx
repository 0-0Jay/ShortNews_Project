import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  Dispatch,
  SetStateAction,
} from "react";
import { useNavigate } from "react-router-dom";

interface SearchKeywordContextProps {
  keyword: string;
  isSearchOpen: number; // 검색창 열림 유무 (0:닫힘, 1:열었다가 닫음, 2:열림)
  isSearchTypeOpen: boolean;
  searchType: number; // 0: 선택안함, 1: 기본검색, 2: 실시간검색
  updateKeyword: Dispatch<SetStateAction<string>>;
  updateIsSearchOpen: Dispatch<SetStateAction<number>>;
  updateIsSearchTypeOpen: Dispatch<SetStateAction<boolean>>;
  updateSearchType: Dispatch<SetStateAction<number>>;
}

const SearchKeywordContext = createContext<
  SearchKeywordContextProps | undefined
>(undefined);

interface SearchKeywordProviderProps {
  children: ReactNode;
}

export const SearchKeywordProvider: React.FC<SearchKeywordProviderProps> = ({
  children,
}: SearchKeywordProviderProps) => {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState<string>("");
  const [isSearchOpen, setIsSearchOpen] = useState<number>(0);
  const [isSearchTypeOpen, setIsSearchTypeOpen] = useState<boolean>(false);
  const [searchType, setSearchType] = useState<number>(0);

  const updateKeyword: Dispatch<SetStateAction<string>> = (newKeyword) => {
    
    setKeyword(newKeyword);
  };

  const updateIsSearchOpen: Dispatch<SetStateAction<number>> = (
    newIsSearchOpen
  ) => {
    setIsSearchOpen(newIsSearchOpen);
  };

  const updateIsSearchTypeOpen: Dispatch<SetStateAction<boolean>> = (
    newIsSearchTypeOpen
  ) => {
    setIsSearchTypeOpen(newIsSearchTypeOpen);
  };

  const updateSearchType: Dispatch<SetStateAction<number>> = (
    newSearchType
  ) => {
    if (newSearchType === 1){
      navigate("/main/search")
    }
    setSearchType(newSearchType);
  };

  return (
    <SearchKeywordContext.Provider
      value={{
        keyword,
        isSearchOpen,
        isSearchTypeOpen,
        searchType,
        updateKeyword,
        updateIsSearchOpen,
        updateIsSearchTypeOpen,
        updateSearchType,
      }}
    >
      {children}
    </SearchKeywordContext.Provider>
  );
};

export const useSearchKeyword = (): SearchKeywordContextProps => {
  const context = useContext(SearchKeywordContext);
  if (!context) {
    throw new Error(
      "useSearchKeyword은 SearchKeywordProvider 내에서 사용되어야 합니다"
    );
  }
  return context;
};
