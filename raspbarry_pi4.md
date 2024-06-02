# Goal : install pyside6 on raspbian for https://github.com/blauret/pyG5
- `sudo apt-get update`
- `sudo apt-get upgrade` und `sudo apt full-upgrade`
- use pip only with virtual env: `python3 -m venv myenv && source myenv/bin/activate && pip3 install pyside6` 
    - did not work : `ERROR: Could not find a version that satisfies the requirement pyside6 (from versions: none) ERROR: No matching distribution found for pyside6``
- use `apt`  instead: `apt-cache search PySide` -> nur PySide2 aber kein PySide6
- `pip install PyQt6` -> Error : `Preparing metadata (pyproject.toml) did not run successfully. exit code: 1`
- `sudo apt install bashtop`

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






