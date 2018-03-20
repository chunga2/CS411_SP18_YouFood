 #!/bin/sh
 ## Bridge the SQL server from the remote machine to the local one
 ssh -NL 5432:localhost:5432 youfood.ddns.net & 
