# Amazon CodeGuru Reviewer Example App

This application is intended to demonstrate Amazon CodeGuru Reviewer, a service that uses program analysis and machine learning to detect potential defects in code. The implementation of this example is deliberately suboptimal, to showcase CodeGuru Reviewer's ability to provide recommendations. This example code should not be used in production. 

To get started with CodeGuru Reviewer, follow the steps below.

## Try CodeGuru Reviewer

### 1) Fork this repo

Log in to GitHub and choose **Fork** to fork this example app to your GitHub account.

### 2) Associate the forked repo

1. Log in to the [CodeGuru dashboard](https://console.aws.amazon.com/codeguru/home?region=us-east-1).
1. Choose **Reviewer** from the left panel and choose **Associated repositories**.
1. Choose **Associate repository**.
1. Make sure **GitHub** is selected and choose **Connect to GitHub**.
1. To allow CodeGuru Reviewer to access your account, choose **Authorize aws-codesuite**. If prompted, confirm your GitHub password.
1. Select the **amazon-codeguru-reviewer-sample-app** repository and choose **Associate**.

CodeGuru Reviewer is now associated with the repo and listening for pull requests.

### 3) Push a change to the code

Clone the forked repo:

    git clone https://github.com/<your-userid>/amazon-codeguru-reviewer-sample-app.git
    
Check out a new branch:

    cd amazon-codeguru-reviewer-sample-app
    git checkout -b dev
    
Copy the Java class at **src/main/java/com/shipmentEvents/handlers/EventHandler.java** into **src/main/java/com/shipmentEvents/demo**. Git and CodeGuru Reviewer will treat this as a new file. 

Push your changes:

    git add --all
    git commit -m 'new demo file'
    git push --set-upstream origin dev
    
### 4) Create a pull request

1. In your forked GitHub repo, choose **New pull request**.
1. On the left side of the comparison (**base**), select **<your-userid>/amazon-codeguru-reviewer-sample-app** and leave the branch at **master**.
1. On the right side of the comparison (**compare**), change the branch to **dev**. They should be shown as mergeable ("Able to merge").
1. Choose **Create pull request** and, again, **Create pull request**.

### 5) Review recommendations

CodeGuru Reviewer will issue recommendations on the same GitHub page where the pull request was created. You can review the recommendations and provide feedback on them.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.

