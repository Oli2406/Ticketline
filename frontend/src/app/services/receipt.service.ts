import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import html2pdf from 'html2pdf.js';


@Injectable({
  providedIn: 'root'
})
export class ReceiptService {

  constructor(private http: HttpClient,
              private globals: Globals) {
  }

  public exportToPDF(): void {
    const options = {
      margin: [10, 10, 10, 10],
      filename: 'invoice.pdf',
      jsPDF: {
        unit: 'mm',
        format: 'a4',
        orientation: 'portrait',
        compress: true,
      },
    };

    const element = document.querySelector('.invoice-container');
    if (element) {
      html2pdf().set(options).from(element).save();
    } else {
      console.error('Invoice container not found.');
    }
  }

}
