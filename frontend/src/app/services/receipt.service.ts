import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";

@Injectable({
  providedIn: 'root'
})
export class ReceiptService {

  constructor(private http: HttpClient,
              private globals: Globals) {
  }

  public exportToPDF() {
    const doc = new jsPDF('p', 'mm', 'a4');

    html2canvas(document.querySelector(".invoice-container"), {
      scale: 2
    }).then(canvas => {
      const imgData = canvas.toDataURL("image/png");
      const imgWidth = 210; // A4 width in mm
      const pageHeight = 297; // A4 height in mm
      const imgHeight = canvas.height * imgWidth / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;

      doc.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        doc.addPage();
        doc.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight, 'FAST');
        heightLeft -= pageHeight;
      }
      doc.setFontSize(20);
      doc.save('pdf-invoice');
    });
  }

  public exportToPDFDownload() {
    const doc = new jsPDF('p', 'mm', 'a4');

    html2canvas(document.querySelector(".invoice-container-download"), {
      scale: 2
    }).then(canvas => {
      const imgData = canvas.toDataURL("image/png");
      const imgWidth = 210; // A4 width in mm
      const pageHeight = 297; // A4 height in mm
      const imgHeight = canvas.height * imgWidth / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;

      doc.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        doc.addPage();
        doc.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight, 'FAST');
        heightLeft -= pageHeight;
      }
      doc.setFontSize(20);
      doc.save('pdf-invoice');
    });
  }
}
