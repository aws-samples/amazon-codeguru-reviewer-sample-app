from flask import render_template, request
import requests

def send_simple_message(email,name):
  return requests.post(
    "https://api.test.net/v3/sandboxbdb9ce1d0ca1923fcc5669628e5ea.test.org/messages",
    auth=("api", "675bdb9ce1d0ca1923fcc5669628b27-acb0b40c-81fd1365"),
    data={"from": "Test Sandbox <postmaster@sandboxbdb9ce1d0ca1923fcc5669628e5ea.test.org>",
            "to": email,
            "subject": name,
            "text": "Congratulations, you just sent an email with Test! You are truly awesome!"})

def ():
    data = request.values
    send_simple_message(data['email'],data['name'])
    return render_template("greeting.html",form_data=data)

def main():
    send_email()

if __name__ == "__main__":
    main()
