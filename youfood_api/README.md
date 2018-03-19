### Flask App ###

This directory contains the flask app in api.py. The wsgi.py is used to connect the Flask App to the Gunicorn server.
Then the Gunicorn server is connected to a Nginx server which sends requests to Gunicorn and sends its responses back out.
The Gunicorn server is run by the system in the background through a Linux Systemd service called "youfood", which allows 
it to run at all times automatically.
The Njinx server is also run on a Systemd service.

Anytime that api.py is updated, you need to run "sudo systemctl restart youfood" to see the changes reflected.
The base URL to communicate with the server is http://35.227.36.41/
