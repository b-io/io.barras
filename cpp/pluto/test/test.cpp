#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    int total = 1 << n; // 2^n possibilities
    for (int mask = 0; mask < total; mask++) {
        for (int bit = n - 1; bit >= 0; --bit) {
            cout << ((mask >> bit) & 1);
        }
        cout << '\n';
    }
    return 0;
}
