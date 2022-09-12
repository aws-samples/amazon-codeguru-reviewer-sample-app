#!/bin/sh
set -x # print commands
set -e # fail on error

echo "Uploading report file $1 "

export user="bineeshravindran"
export password=$BB_API_TOKEN


# export BB_URL="https://api.github.com/repos/bineeshcertified/amazon-codeguru-reviewer-sample-app/pulls/1/comments" 

# curl --request PUT "${BB_URL}" --header "Content-Type: application/json"  -u $user:$password --data "@${1}" 

# echo "Uploaded report"
# curl  --location  "${BB_URL}/annotations" --header "Content-Type: application/json"  -u $user:$password --data "@${2}"

# echo "Done."


curl \
  -X PATCH \
  -H "Accept: application/vnd.github+json" \ 
  -H "Authorization: Bearer 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" \
  https://api.github.com/repos/bineeshcertified/amazon-codeguru-reviewer-sample-app/pulls/1/comments \
  -d '{"body":"Great stuff!","commit_id":"6dcb09b5b57875f334f61aebed695e2e4193db5e","path":"file1.txt","start_line":1,"start_side":"RIGHT","line":2,"side":"RIGHT"}'