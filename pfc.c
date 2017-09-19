#include <stdlib.h>
#include <string.h>

int main() {
    char cwd[2024];
    strcat(cwd, "/bin/sh ");
    getcwd(cwd, sizeof(cwd));
    strcat(cwd, "/run.sh");
    system(cwd);
    return 0;
}
