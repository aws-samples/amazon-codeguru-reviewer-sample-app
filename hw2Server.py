# import socket module
from socket import *
import sys # In order to terminate the program

serverSocket = socket (AF_INET, SOCK_STREAM) #create TCP welcoming socket

# Prepare a server socket on a particular port
# Fill in code to set up the port
serverPort = 6789 #hardwired
serverSocket.bind(('', serverPort))
serverSocket.listen(1) #server begins listening for incoming TCP requests

HTTPSuccessHeader = 'HTTP/1.1 200 OK\r\n'+'Content-Type: text/html\n'+'\n'
HTTP403ErrorHeader = 'HTTP/1.1 403 OK\n'+'Content-Type: text/html\n' +'\n'
HTTP404ErrorHeader = 'HTTP/1.1 404 OK\n'+'Content-Type: text/html\n' +'\n'

while True :
    # Establish the connection
    print('Ready to serve...')
    connectionSocket , addr = serverSocket.accept() # Fill in code to get a connection
    try:
        message = connectionSocket.recv(1024).decode()# Fill in code to read GET request
        print('message: ')
        print(message)
        filename = message.split()[1] 
        print('filename: ')
        print(filename)

        secondSlash = filename.find('/', 1)#the second slash in the filename indicates leaving that directory
                                        #find returns num > -1, when it finds the 'str'
        if(secondSlash > -1):
            f = open('Error403.html') 
            # Send the HTTP header line(s) into socket
            connectionSocket.send(HTTP403ErrorHeader.encode())
        elif(filename[0] == '/'):
            filename = filename.replace('/','',1)
            f = open(filename)
            # Send the HTTP header line(s) into socket
            connectionSocket.send(HTTPSuccessHeader.encode())
        else: #cannot find file
            f = open(filename)
            # Send the HTTP header line(s) into socket
            connectionSocket.send(HTTP404ErrorHeader.encode())

        # Send HTTP header line (s) into socket
        # Fill in code to send header (s)

        # Send the content of the requested file to the client
        while True:
            chunk = f.read(2000)
            if chunk =='':
                break
            else:
                connectionSocket.send(chunk.encode())


        connectionSocket.send("\r\n".encode())
        connectionSocket.close() 
    except IOError :
        # Send response message for file not found
        # Fill in
        f = open('Error404.html') #open the error404html that I made in the same folder
        connectionSocket.send(HTTP404ErrorHeader.encode())
        
        # Send the content of the requested file to the client
        while True:
            chunk = f.read(2000)
            if chunk == '':
                break
            else:
                connectionSocket.send(chunk.encode())
        # Close client socket
        # Fill in
        connectionSocket.send("\r\n".encode())
        connectionSocket.close()
    serverSocket.close()
    sys.exit() # Terminate the program after sending the corresponding data