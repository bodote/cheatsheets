[Back to Table of Contents](README.md)
# OpenStack
## auf Mac, (python27 geht nicht):
* virtual environment installieren 
* siehe : https://uoa-eresearch.github.io/eresearch-cookbook/recipe/2014/11/26/python-virtual-env/
* siehe : https://stackoverflow.com/questions/31133050/virtualenv-command-not-found

## besser mit python3 und vscode: 
* https://www.python.org/dev/peps/pep-0405/#api
* https://code.visualstudio.com/docs/python/environments#_where-the-extension-looks-for-environments  (suche nach "create a virtual environment")
* ein projektverzeichnis anlegen , z.B. "myproject" und dort drin ein .venv - unterverzeichnis
```bash
mkdir myproject
cd myproject
python3 -m venv .venv
```
* hier müsste vscode jetzt fragen ob ".venv" als virtual env verwendet werden soll
* alles was hier noch an python libs installiert werden soll dann mit:
```bash
cd .venv
pip3 install pandas
```
* dann ist das  starten virtual python environment in VS-Code überflüssig , denn das macht dann VS-Code automatisch. 
* in einer Normal shell jedoch muss man : 
  `[bodo@Bodos-MBP:~/swe_projects/ansible_virtenv/]$ source .venv/bin/activate`
* oder eben 
  `[bodo@Bodos-MBP:~/swe_projects/op_stack/] source .venv/bin/activate`
dann kann man im Unterverzeichniss `.venv` mit 
`[bodo@Bodos-MBP:~/swe_projects/op_stack/.venv] pip install python-openstackclient`
installieren

### Links
* https://help.dreamhost.com/hc/en-us/articles/235817468-Getting-started-with-the-OpenStack-command-line-client
* `openstack --version`
* https://docs.openstack.org/newton/user-guide/common/cli-discover-version-number-for-a-client.html

### Netways OpenStack  nutzen, einmalige Installation 
dann von https://cloud.netways.de/ (login mit user:openstack-account-name) die openenstack RC datei (oben rechts ) runterladen und ins vor per `python3 -m venv` erzeugte directory kopieren, dann mit 
`source openstack-account-name-openrc.sh`
aufrufen mit passwort von  `user:openstack-account-name`
evtl diese Passwort in die `openstack-account-name-openrc.sh` reinkopieren, dannach müsste  `openstack image list`
funktionieren.  
### Netways nutzten , nach der Installation
wenn das obige alles schon installiert ist, aber man eine shell neu starten will
muss man VOR dem openstack befehlen immer erst  ein 
```bash
source bin/activate
source openstack-account-name-openrc.sh
```
eingeben um die virtuelle python umgebung zu starten und die openStack umgebung zu konfigurieren 
Dannach dann müsste ein `openstack image list` funktionieren, wenn KEIN VPN oder ähnliches die Verbindung auf den hohen Ports blockiert


