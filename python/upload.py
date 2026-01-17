import boto3
import datetime
from database import selectHour

def s3_connection():
    try:
        # s3 클라이언트 생성
        s3 = boto3.client(
            service_name="s3",
            region_name="ap-northeast-2",
            aws_access_key_id="AKIAXUNXPSJYIRVGLZPX",
            aws_secret_access_key="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",
        )
    except Exception as e:
        print(e)
    else:
        print("연결 성공") 
        return s3


# TTS mp3 업로드
def upload_mp3():
    s3 = s3_connection()
    df = selectHour()

    for i in range(len(df)):
    # for i in range(2):  # 테스트용
        try:
            s3.upload_file(f"tts_mp3/{df['NEWS_ID'][i]}_female.mp3", "snewstts", f"{df['NEWS_ID'][i]}_female.mp3") # "{로컬 파일 이름}, {버킷 이름}, {실제로 저장될 이름}"
            s3.upload_file(f"tts_mp3/{df['NEWS_ID'][i]}_male.mp3", "snewstts", f"{df['NEWS_ID'][i]}_male.mp3") # "{로컬 파일 이름}, {버킷 이름}, {실제로 저장될 이름}"
        except Exception as e:
            print(f"{df['NEWS_ID'][i]} mp3 업로드 실패", e)
    
    print("mp3 업로드 성공")
