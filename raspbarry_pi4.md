# Goal : install pyside6 on raspbian for https://github.com/blauret/pyG5
- use 64bit system , because pyside6 is not available on 32bit variant.
- `sudo apt-get update`
- `sudo apt-get upgrade` und `sudo apt full-upgrade`
- use pip only with virtual env: `python3 -m venv myenv && source myenv/bin/activate && pip3 install pyside6` 
- `sudo apt install btop vim`
- `sudo apt install libxcb-xinerama0 libxcb-cursor0 libxkbcommon-x11-0` for pyside6 to work
- `sudo vim /etc/ssh/sshd_config` and set:
```
ClientAliveInterval 60
ClientAliveCountMax 5
```
on the ssh - terminal side set : `vim ~/.ssh/config` : 
```
Host *
    ServerAliveInterval 60
    ServerAliveCountMax 5
```
- `sudo systemctl restart ssh`

## install display driver for 7inch HDMI Display-C from lafvintech.com
https://lafvintech.com/pages/tutorials -> 7inch HDMI Display-C
http://www.lcdwiki.com/7inch_HDMI_Display-C 
- first install driver , see "How to rotate the display direction" 
- then reboot
- then rotate





## install github client `gh`
```bash
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
sudo chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
sudo apt update
sudo apt install gh
```
- `gh auth login`
## install pyG5 from sources
- `gh repo clone bodote/pyG5`
- `gh repo set-default`
- `source myenv/bin/activate`
- `source bootstrap.sh`
## start pyG5 tester
```
export DISPLAY=:0
DISPLAY=:0 xhost +local:
python -m pyG5.pyG5ViewTester
```
## start the real thing:
```
export DISPLAY=:0
DISPLAY=:0 xhost +local:
python -m pyG5.pyG5Main
```

## problems 
- font to big, some characters are not shown `sudo apt-get install libfreetype6` does not help






## Build QT6 for Python
since installing QT6 or even pyside6 did not work with apt or pip, try: 
- https://davy.ai/qt6-on-raspberry-os-how-to-install-and-use-pyside6/ did not work at all
- https://doc.qt.io/qtforpython-6/gettingstarted/linux.html but that needs Qt6.4+ first
- https://www.tal.org/tutorials/building-qt-65-raspberry-pi-raspberry-pi-os
    - nearly worked except for the missing `Libdrm::Libdrm` so `sudo apt install libdrm-dev libgbm-dev libegl-dev libgl-dev` helped
    - instead of `cmake --install .` use `sudo cmake --install .`
- continue with https://doc.qt.io/qtforpython-6/gettingstarted/linux.html
    - `sudo apt-get install clang`
    -  trying : "Building and Installing (setuptools)"  but instead of `python setup.py build --qtpaths=/opt/Qt/6.5.0/gcc_64/bin/qtpaths --build-tests --ignore-git --parallel=8` 
        - I use `python setup.py build --qtpaths=/opt/Qt/6.5/bin/qtpaths --build-tests --ignore-git --parallel=4` instead 
        - did not work 
```
[INFO]: Configuring module shiboken6 (/home/bodo/pyside-setup/sources/shiboken6)...
-- The C compiler identification is GNU 12.2.0
-- The CXX compiler identification is GNU 12.2.0
-- Detecting C compiler ABI info
-- Detecting C compiler ABI info - done
-- Check for working C compiler: /usr/bin/cc - skipped
-- Detecting C compile features
-- Detecting C compile features - done
-- Detecting CXX compiler ABI info
-- Detecting CXX compiler ABI info - done
-- Check for working CXX compiler: /usr/bin/c++ - skipped
-- Detecting CXX compile features
-- Detecting CXX compile features - done
-- SHIBOKEN_IS_CROSS_BUILD: FALSE
-- SHIBOKEN_BUILD_LIBS: ON
-- SHIBOKEN_BUILD_TOOLS: ON
-- BUILD_TESTS: 1
-- Using Qt 6
-- Performing Test CMAKE_HAVE_LIBC_PTHREAD
-- Performing Test CMAKE_HAVE_LIBC_PTHREAD - Success
-- Found Threads: TRUE  
-- Performing Test HAVE_STDATOMIC
-- Performing Test HAVE_STDATOMIC - Failed
-- Performing Test HAVE_STDATOMIC_WITH_LIB
-- Performing Test HAVE_STDATOMIC_WITH_LIB - Success
-- Found WrapAtomic: TRUE  
CMake Error at cmake/ShibokenHelpers.cmake:194 (_message):
Call Stack (most recent call first):
  /usr/lib/llvm-14/lib/cmake/clang/ClangTargets.cmake:756 (message)
  /usr/lib/cmake/clang-14/ClangConfig.cmake:19 (include)
  cmake/ShibokenHelpers.cmake:170 (find_package)
  cmake/ShibokenSetup.cmake:38 (setup_clang)
  CMakeLists.txt:14 (include)
```
CMakeError.log:
```
Performing C++ SOURCE FILE Test HAVE_STDATOMIC failed with the following output:
Change Dir: /home/bodo/pyside-setup/build/myenv/build/shiboken6/CMakeFiles/CMakeScratch/TryCompile-aJvDOe

Run Build Command(s):/usr/bin/ninja cmTC_ba179 && [1/2] Building CXX object CMakeFiles/cmTC_ba179.dir/src.cxx.o
[2/2] Linking CXX executable cmTC_ba179
FAILED: cmTC_ba179 
: && /usr/bin/c++   CMakeFiles/cmTC_ba179.dir/src.cxx.o -o cmTC_ba179   && :
/usr/bin/ld: CMakeFiles/cmTC_ba179.dir/src.cxx.o: in function `main':
src.cxx:(.text+0x248): undefined reference to `__atomic_load_8'
/usr/bin/ld: CMakeFiles/cmTC_ba179.dir/src.cxx.o: in function `std::__atomic_base<long long>::operator++() volatile':
src.cxx:(.text._ZNVSt13__atomic_baseIxEppEv[_ZNVSt13__atomic_baseIxEppEv]+0x28): undefined reference to `__atomic_fetch_add_8'
collect2: error: ld returned 1 exit status
ninja: build stopped: subcommand failed.
```

13FF-DF56





