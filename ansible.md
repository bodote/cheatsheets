### Ansible

`ansible-playbook foo.yml --check --diff --limit <only.one.host>` 

`--check` simulate

`--diff` show difference

`--limit host` only this host

--syntax-check

im Playbook, play or in a task:  
`debugger: on_failed`

in ansible.cfg: 
`[defaults]
enable_task_debugger = True`

or as  environment variable:
`ANSIBLE_ENABLE_TASK_DEBUGGER=True; ansible-playbook -i hosts site.yml`


humen readable output: 
`export ANSIBLE_STDOUT_CALLBACK=debug `
oder in ansible.cfg:
`human-readable` 

stdout/stderr results display

stdout_callback = debug
