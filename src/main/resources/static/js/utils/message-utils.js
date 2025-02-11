
export class MessageUtils {
    static MESSAGE_TYPE_DANGER = "toast-body text-bg-danger";
    static MESSAGE_TYPE_SUCCESS = "toast-body text-bg-success";
    static MESSAGE_TYPE_PRIMARY = "toast-body text-bg-primary";
    static danger(message = "Danger", title="Danger", toastContainer = "toastContainer") {
        MessageUtils.message(MessageUtils.MESSAGE_TYPE_DANGER, message, title, toastContainer);
    }

    static success(message = "Success", title="Success", toastContainer = "toastContainer") {
        MessageUtils.message(MessageUtils.MESSAGE_TYPE_SUCCESS, message, title, toastContainer);
    }

    static message( type = MessageUtils.MESSAGE_TYPE_PRIMARY,message="not message", title="message", toastContainer= "toastContainer") {
        if(message == null) return;
        const parent = document.getElementById(toastContainer);

        const div = document.createElement("div");
        div.classList.add("toast");
        div.role = "alert";
        div.ariaLive = "assertive";
        div.ariaAtomic = "true";
        div.innerHTML = `
        <div class="toast-header">
            <strong class="me-auto">${title}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
        <div class="${type}">${message}</div>
        `;

        parent.appendChild(div);
        div.addEventListener("hidden.bs.toast", () => {
            parent.removeChild(div);
        });
        const toast = new window.bootstrap.Toast(div);
        if(toast) toast.show();
    }

}