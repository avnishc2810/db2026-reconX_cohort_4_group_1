(function () {

    const table = document.getElementById("trades-table");

    if (!table) return;

    const tbody = document.getElementById("trades-tbody");

    let rows = [

        {
            tradeRef: "TRD-001",
            symbol: "AAPL",
            quantity: 100,
            price: 120,
            status: "MATCHED"
        },

        {
            tradeRef: "TRD-002",
            symbol: "MSFT",
            quantity: 50,
            price: 250,
            status: "BREAK"
        },

        {
            tradeRef: "TRD-003",
            symbol: "SAP",
            quantity: 75,
            price: 300,
            status: "PENDING"
        }

    ];

    function render() {

        tbody.innerHTML = rows.map(r => `

<tr>

<td>${r.tradeRef}</td>

<td>${r.symbol}</td>

<td>${r.quantity}</td>

<td>${r.price}</td>

<td>${r.status}</td>

</tr>

`).join("");

    }

    render();

    table.querySelectorAll("thead th").forEach(th => {

    th.addEventListener("click", (e) => {

        if (e.target.classList.contains("resize-handle"))
            return;

        const col = th.dataset.col;
        const type = th.dataset.type || "string";

        const dir =
            th.getAttribute("aria-sort") === "ascending"
                ? "descending"
                : "ascending";

        table.querySelectorAll("thead th")
            .forEach(h => h.removeAttribute("aria-sort"));

                th.setAttribute("aria-sort", dir);

                const mult = dir === "ascending" ? 1 : -1;

                rows.sort((a, b) => {

                    if (type === "number")
                        return (Number(a[col]) - Number(b[col])) * mult;

                    return String(a[col])
                        .localeCompare(String(b[col])) * mult;

                });

                render();

            });

        });

   table.querySelectorAll(".resize-handle").forEach(handle => {

        handle.addEventListener("mousedown", (e) => {

            e.preventDefault();

            const th = handle.closest("th");

            const startX = e.clientX;

            const startWidth = th.offsetWidth;

            function onMove(ev) {

                th.style.width =
                    (startWidth + ev.clientX - startX) + "px";

            }

            function onUp() {

                document.removeEventListener(
                    "mousemove",
                    onMove
                );

                document.removeEventListener(
                    "mouseup",
                    onUp
                );

            }

            document.addEventListener(
                "mousemove",
                onMove
            );

            document.addEventListener(
                "mouseup",
                onUp
            );

        });

    });

})();