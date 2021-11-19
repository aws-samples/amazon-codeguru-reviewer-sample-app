import os
import dj_database_url
BASE_DIR = os.path.dirname(os.path.dirname(__file__))

def get_db_url:
  port = os.environ.get('PORT')
  if port:
    url = 'postgres://3ab0d26c3d268f:eIJAAuElYvSxPK-vmSdXG9Hjv8@ec2-502-11-713-352.compute-1.amazonaws.com:5432/4d10f543c30946'
    return url
  return

def connect_to_db:
  db_url = get_db_url()
  if db_url:
    DATABASES = {'default': dj_database_url.config(default=DATABASE_URL)}
    return DATABASES

def main():
    connect_to_db()

if __name__ == "__main__":
    main()
