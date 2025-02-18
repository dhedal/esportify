/**
 *
 */
export class Pagination {
    constructor(containerId, onPageChange) {
        this.container = document.getElementById(containerId);
        this.onPageChange = onPageChange;
        this.currentPage = 1;
        this.totalPages = 1;
    }

    /**
     *
     * @param totalPages
     */
    updatePagination(totalPages) {
        this.totalPages = totalPages;
        this.render();
    }

    /**
     *
     */
    render() {
        this.container.innerHTML = "";
        if (this.totalPages <= 1) return;

        let prevDisabled = this.currentPage === 1 ? "disabled" : "";
        let nextDisabled = this.currentPage === this.totalPages ? "disabled" : "";

        this.container.innerHTML = `
            <li class="page-item ${prevDisabled}">
                <a class="page-link" href="#" data-page="${this.currentPage - 1}">Précédent</a>
            </li>
            ${Array.from({ length: this.totalPages }, (_, i) => `
                <li class="page-item ${i + 1 === this.currentPage ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i + 1}">${i + 1}</a>
                </li>
            `).join('')}
            <li class="page-item ${nextDisabled}">
                <a class="page-link" href="#" data-page="${this.currentPage + 1}">Suivant</a>
            </li>
        `;

        this.container.querySelectorAll(".page-link").forEach(link => {
            link.addEventListener("click", (event) => {
                event.preventDefault();
                let page = parseInt(event.target.getAttribute("data-page"));
                if (page >= 1 && page <= this.totalPages) {
                    this.currentPage = page;
                    this.onPageChange(page);
                    this.render();
                }
            });
        });
    }
}