import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'highlight' })
export class HighlightPipe implements PipeTransform {
    transform(text: string, search): string {
        if (search) {
            const pattern = search
                .replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")
                .split(' ')
                .filter(t => t.length > 0)
                .join('|');
            const regex = new RegExp(pattern, 'gi');

            return (search && text) ? text.replace(regex, match => `<b class="bg-searched-latter">${match}</b>`) :
                text;
        } else {
            return text;
        }
    }
}