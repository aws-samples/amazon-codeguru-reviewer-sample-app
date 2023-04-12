ranNum=$[RANDOM%100+1]
#echo $ranNum
branchName="feature"$ranNum
echo "Checking out to branch name: "$branchName
git checkout -b $branchName
mkdir src/main/java/com/shipmentEvents/$branchName
cp src/main/java/com/shipmentEvents/handlers/EventHandler.java src/main/java/com/shipmentEvents/$branchName/
echo "Created dummy code in $branchName folder"
git add --all
git commit -m "cr3 demo"
git push --set-upstream origin $branchName
echo "Committed and pushed dummy code to remote $branchName "

